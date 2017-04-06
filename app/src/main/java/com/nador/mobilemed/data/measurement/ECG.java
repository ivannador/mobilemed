package com.nador.mobilemed.data.measurement;

import android.content.Context;

import com.nador.mobilemed.data.utils.FileUtils;
import com.nador.mobilemed.data.entity.measurement.ECGGraph;
import com.nador.mobilemed.presentation.utils.DateUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by nador on 11/08/16.
 */
public class ECG extends HealthDevice {

    private static final short ECG_VALUE_BITMASK = 0x3FF;

    @Inject
    public ECG() {
        super(new ECGAddressProvider());
    }

    public static final class ECGAddressProvider extends HealthDeviceAddressProvider {
        @Override
        public String getHealthDeviceAddress(Context context) {
            // TODO: hardcoded for testing purposes, get it through webservice (not implemented yet)
            return "".toUpperCase();
        }
    }

    private static ECGGraph parseECGHeader(final File heaFile) {
        ECGGraph graph = null;
        if (heaFile.exists()) {
            try {
                FileInputStream is = new FileInputStream(heaFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                reader.close();
                String[] parts = line.split("\\s+");
                if (parts.length < 6) {
                    throw new Exception();
                }

                String name = parts[0];
                int channels = Integer.parseInt(parts[1]);
                float samplingRate = Float.parseFloat(parts[2]);
                long date = DateUtil.getTimestamp(parts[4] + " " + parts[5], "HH:mm:ss dd/MM/yyyy");

                switch (channels) {
                    case 1:
                        graph = new ECGGraph.SingleChannel();
                        break;
                    case 2:
                        graph = new ECGGraph.DualChannel();
                        break;
                    case 3:
                        graph = new ECGGraph.TriChannel();
                        break;
                    default:
                        break;
                }

                graph.setName(name);
                graph.setSamplingRate(samplingRate);
                graph.setDate(date);
            } catch (Exception ex) {
                Timber.e(ex, "Can't parse ECG hea file");
            }
        }
        return graph;
    }

    public static ECGGraph getGraphFromDir(final File dir) {
        ECGGraph graph = null;
        for (File file : dir.listFiles()) {
            if (FileUtils.getFileExtension(file).equals(".hea")) {
                graph = parseECGHeader(file);
                break;
            }
        }

        if (graph != null) {
            List<Integer> values = new ArrayList<>();
            try {
                InputStream is = new FileInputStream(dir.getAbsolutePath() + "/" + graph.getName() + ".dat");
                byte[] buffer = new byte[4];

                while (is.read(buffer) != -1) {
                    ByteBuffer byteWrap = ByteBuffer.wrap(buffer);
                    byteWrap.order(ByteOrder.LITTLE_ENDIAN);
                    int val = byteWrap.getInt();
                    int val1 = (val & ECG_VALUE_BITMASK);
                    int val2 = ((val >> 10) & ECG_VALUE_BITMASK);
                    int val3 = ((val >> 20) & ECG_VALUE_BITMASK);

                    if (val1 >= 512) {
                        val1 -= 1024;
                    }
                    if (val2 >= 512) {
                        val2 -= 1024;
                    }
                    if (val3 >= 512) {
                        val3 -= 1024;
                    }
                    values.add(val1);
                    values.add(val2);
                    values.add(val3);
                }
                is.close();

                ((ECGGraph.SingleChannel) graph).setFirstValueList(values);
            } catch (FileNotFoundException ex) {
                Timber.e(ex, "File not found");
            } catch (IOException ex) {
                Timber.e(ex, "IOException");
            }
        }

        return graph;
    }
}
