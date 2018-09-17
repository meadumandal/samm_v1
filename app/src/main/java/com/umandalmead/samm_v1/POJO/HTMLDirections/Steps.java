package com.umandalmead.samm_v1.POJO.HTMLDirections;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by eleazerarcilla on 07/01/2018.
 */

public class Steps {
    @SerializedName("html_instructions")
    @Expose
    private String Instruction;
    /**
     *
     * @return
     * The text
     */
    public String getSteps() {
        return Instruction;
    }
}
