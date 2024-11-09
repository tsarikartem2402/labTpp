package ttp.lab3;

import lombok.Data;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public record Student(int id,String first_name,String last_name,int year,String group_name) {

}