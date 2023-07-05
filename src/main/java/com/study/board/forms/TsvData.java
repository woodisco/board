package com.study.board.forms;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TsvData {

    @NotNull(message = "numは必須です。")
    @Size(min = 18, message = "Age should not be less than 18")
    private String num;


    @Size(min = 18, message = "Age should not be less than 18")
    private String name;


    @Size(min = 18, message = "Age should not be less than 18")
    private String email;
}
