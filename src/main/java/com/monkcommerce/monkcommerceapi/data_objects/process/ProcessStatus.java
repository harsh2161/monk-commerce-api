package com.monkcommerce.monkcommerceapi.data_objects.process;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProcessStatus
{
    private boolean isSuccess;
    private String message;
    public ProcessStatus(boolean isSuccess,String message)
    {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
