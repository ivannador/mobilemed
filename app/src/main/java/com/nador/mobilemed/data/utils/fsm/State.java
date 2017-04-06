package com.nador.mobilemed.data.utils.fsm;

/**
 * State with customizable operation
 */
public interface State {
    void onState(StateContext context);
}
