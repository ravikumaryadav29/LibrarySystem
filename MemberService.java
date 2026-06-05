package service;

import model.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MemberService — manages library members.
 *
 * Operations: Add, Update, Delete, Search, View all
 */
public class MemberService {

    private List<Member> members;
    private FileService  fileService;
    private int          nextId;

    public MemberService(FileService fileService) {
        this.fileService = fileService;
        this.members     = fileService.loadMembers();
        this.nextId      = computeNextId();
    }

    // ── Add ──────────────────────────────────────────────────────

    public Member addMember(String name, String email, String phone) {
        // Check duplicate email
        if (members.stream().anyMatch(m -> m.getEmail().equalsIgnoreCase(email))) {
            return null; // duplicate
        }
        String id = String.format("M%03d", nextId++);
        Member member = new Member(id, name, email, phone);
        members.add(member);
        save();
        return member;
    }

    // ── Update ───────────────────────────────────────────────────

    public boolean updateMember(String memberId, String name,
                                String email, String phone) {
        Member m = findById(memberId);
        if (m == null) return false;
        m.setName(name);
        m.setEmail(email);
        m.setPhone(phone);
        save();
        return true;
    }

    // ── Delete ───────────────────────────────────────────────────

    /** Cannot delete a member who has books currently borrowed. */
    public boolean deleteMember(String memberId) {
        Member m = findById(memberId);
        if (m == null) return false;
        if (!m.getBorrowedBookIds().isEmpty()) return false;
        members.remove(m);
        save();
        return true;
    }

    // ── Search ───────────────────────────────────────────────────

    public Member findById(String memberId) {
        return members.stream()
                .filter(m -> m.getMemberId().equalsIgnoreCase(memberId))
                .findFirst().orElse(null);
    }

    public List<Member> searchByName(String keyword) {
        String kw = keyword.toLowerCase();
        return members.stream()
                .filter(m -> m.getName().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    // ── Views ─────────────────────────────────────────────────────

    public List<Member> getAllMembers()    { return new ArrayList<>(members); }

    public List<Member> getMembersWithBooks() {
        return members.stream()
                .filter(m -> !m.getBorrowedBookIds().isEmpty())
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────

    private void save() { fileService.saveMembers(members); }

    private int computeNextId() {
        return members.stream()
                .mapToInt(m -> {
                    try { return Integer.parseInt(m.getMemberId().substring(1)); }
                    catch (Exception e) { return 0; }
                })
                .max().orElse(0) + 1;
    }
}
