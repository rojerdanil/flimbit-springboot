package com.riseup.flimbit.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserContext {
	private int userId;
    private String phone;
    private String name;
    private String type;

}
