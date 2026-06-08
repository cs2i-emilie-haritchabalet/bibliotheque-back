import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswords {
    public static void main(String[] args) {
        var encoder = new BCryptPasswordEncoder();
        System.out.println("admin123 → " + encoder.encode("admin123"));
        System.out.println("alice123 → " + encoder.encode("alice123"));
    }
}
