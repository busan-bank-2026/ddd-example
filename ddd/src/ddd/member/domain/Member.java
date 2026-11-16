package ddd.member.domain;

public class Member {

    private final long id;
    private final String name;
    private final MemberGrade grade;

    public Member(long id, String name, MemberGrade grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MemberGrade getGrade() {
        return grade;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member other)) {
            return false;
        }
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
