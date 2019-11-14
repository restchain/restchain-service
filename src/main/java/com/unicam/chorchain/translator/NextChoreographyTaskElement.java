package com.unicam.chorchain.translator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.ap.internal.model.common.ModelElement;

@NoArgsConstructor
@Getter
@Setter
public  class NextChoreographyTaskElement {
    private boolean isGatwayOrEnd;
    private ModelElement element;

}
