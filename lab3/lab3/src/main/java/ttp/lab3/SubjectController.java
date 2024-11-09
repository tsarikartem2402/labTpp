package ttp.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Subject> getSubjectById(@PathVariable int id) {
        return subjectRepository.findById(id);
    }

    @PostMapping
    public String createSubject(@RequestBody Subject subject) {
        subjectRepository.save(subject);
        return "Subject created successfully!";
    }

    @PutMapping("/{id}")
    public String updateSubject(@PathVariable int id, @RequestBody Subject updatedSubject) {
        updatedSubject = new Subject(id, updatedSubject.name(), updatedSubject.credits(), updatedSubject.department_id());
        subjectRepository.update(updatedSubject);
        return "Subject updated successfully!";
    }

    @DeleteMapping("/{id}")
    public String deleteSubject(@PathVariable int id) {
        subjectRepository.deleteById(id);
        return "Subject deleted successfully!";
    }
}
