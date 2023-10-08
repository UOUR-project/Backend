package com.backend.uour.global.network;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class ResultDTO<D> {
    private final STATUS status;
    private final D data;
}


