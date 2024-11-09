package ttp.lab3;


import lombok.Data;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public record Teacher(int id,String first_Name,String last_Name,String position,Long department_id) {

}