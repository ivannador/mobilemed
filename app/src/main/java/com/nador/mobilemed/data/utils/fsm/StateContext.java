package com.nador.mobilemed.data.utils.fsm;

/**
 * State context for a state
 *
 * Created by nador on 29/06/16.
 */
public interface StateContext {

    State state();

    void state(final State state);
}

