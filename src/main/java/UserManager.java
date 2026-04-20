import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserManager implements Repository<User> {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public synchronized void add(User item) {
        if (item == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (users.containsKey(item.username())) {
            throw new IllegalArgumentException("User with username '" + item.username() + "' already exists");
        }
        users.put(item.username(), item);
    }

    @Override
    public synchronized boolean remove(User item) {
        if (item == null) {
            return false;
        }
        return users.remove(item.username()) != null;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int count() {
        return users.size();
    }

    @Override
    public synchronized void clear() {
        users.clear();
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.email().equals(email))
                .findFirst();
    }

    public List<User> findByFilter(UserFilter filter) {
        return users.values().stream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<User> findAll(UserFilter filter, Comparator<User> sorter) {
        return users.values().stream()
                .filter(filter::test)
                .sorted(sorter)
                .collect(Collectors.toList());
    }

    public List<User> findByFilterParallel(UserFilter filter) {
        return users.values().parallelStream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }

    public List<User> findAllParallel(UserFilter filter, Comparator<User> sorter) {
        return users.values().parallelStream()
                .filter(filter::test)
                .sorted(sorter)
                .collect(Collectors.toList());
    }

    public boolean exists(String username) {
        return users.containsKey(username);
    }

    public synchronized void update(String username, String newFullName, String newEmail) {
        User existingUser = users.get(username);
        if (existingUser == null) {
            throw new IllegalArgumentException("User with username '" + username + "' not found");
        }

        // Валидация новых данных
        if (newFullName == null || newEmail == null) {
            throw new IllegalArgumentException("Full name and email cannot be null");
        }
        ValidationUtils.requireNonEmpty(newFullName, "fullName");
        ValidationUtils.requireNonEmpty(newEmail, "email");
        if (!ValidationUtils.isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Некорректный формат email!");
        }

        // Проверка на дубликат email (если email изменился)
        if (!existingUser.email().equals(newEmail)) {
            Optional<User> userWithEmail = findByEmail(newEmail);
            if (userWithEmail.isPresent()) {
                throw new IllegalArgumentException("User with email '" + newEmail + "' already exists");
            }
        }

        // Создаем нового пользователя с обновленными данными
        User updatedUser = new User(
                username,
                ValidationUtils.normalizeString(newFullName),
                ValidationUtils.normalizeString(newEmail)
        );
        users.put(username, updatedUser);
    }
}
