package com.umandalmead.samm_v1.Interfaces;

import com.umandalmead.samm_v1.EntityObjects.Terminal;

import java.util.List;

/**
 * Created by eleazerarcilla on 21/11/2017.
 */

public interface IDestination {
    void act(List<Terminal> models);
}
