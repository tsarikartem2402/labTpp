package ttp.lab3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public record Subject(Integer id,String name,int credits,int department_id) {

}
