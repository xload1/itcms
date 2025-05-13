package main.java.com.wspa.courses.services;

@Service
public class EnrollmentService {

    private final EnrollmentRepository repo;

    public EnrollmentService(EnrollmentRepository repo) { this.repo = repo; }

    public List<Enrollment> forUser(Users u) { return repo.findByStudent(u); }

    public Enrollment enrollIfAbsent(Users user, Course course) {
        return repo.findByStudentAndCourseId(user, course.getId())
                .orElseGet(() -> {
                    Enrollment e = new Enrollment();
                    e.setStudent(user);
                    e.setCourse(course);
                    return repo.save(e);
                });
    }
}