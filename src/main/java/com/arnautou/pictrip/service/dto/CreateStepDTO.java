package com.arnautou.pictrip.service.dto;

import java.io.Serializable;

/**
 * Created by mario on 08/08/17.
 */
public class CreateStepDTO implements Serializable {

    private PlaceDTO placeDTO;

    private StepDTO stepDTO;

    private JourneyDTO journeyDTO;

    public PlaceDTO getPlaceDTO() {
        return placeDTO;
    }

    public void setPlaceDTO(PlaceDTO placeDTO) {
        this.placeDTO = placeDTO;
    }

    public StepDTO getStepDTO() {
        return stepDTO;
    }

    public void setStepDTO(StepDTO stepDTO) {
        this.stepDTO = stepDTO;
    }

    public JourneyDTO getJourneyDTO() {
        return journeyDTO;
    }
}
