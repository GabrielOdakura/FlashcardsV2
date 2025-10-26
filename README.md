# 📚 Flashcards App

A modern **Android Flashcards App** built with Java and SQLite, designed to help users create, manage, and study decks of flashcards with performance tracking and personalized statistics.

---

## 🚀 Features

- 👤 **User Profiles**
    - Each user has their own decks, stats, and progress.
    - Profile screen displays overall accuracy and weekly study sessions graph.

- 🗂️ **Deck Management**
    - Create, edit, and delete decks dynamically.
    - Each deck contains flashcards linked to the logged-in user.

- 🧠 **Flashcard Practice**
    - Practice mode tracks correct and incorrect answers.
    - Session results are saved in the database (`deck_sessions` table).

- 📊 **Statistics Dashboard**
    - Circular chart showing correct vs. wrong answers.
    - Weekly sessions bar graph displaying study activity over time.

- 💾 **Local Storage**
    - Uses SQLite for persistent storage.
    - `SharedPreferences` handles session management and current user tracking.

- 🔄 **Auto Refresh**
    - Profile and main screens automatically refresh when resuming the app or switching users.

---

## 🧩 Tech Stack

| Component | Description |
|------------|-------------|
| **Language** | Java |
| **Database** | SQLite (via `SQLiteOpenHelper`) |
| **UI** | Android XML layouts |
| **Charts** | Custom views (`CustomStatsView`, `WeeklySessionsView`) |
| **Persistence** | `SharedPreferences` for user session |
| **Minimum SDK** | 24 (Android 7.0 Nougat) |

---

## ⚙️ Database Schema

### 🧑‍💻 `users`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | User ID |
| `nickname` | TEXT | Display name |
| `email` | TEXT UNIQUE | User email |
| `password` | TEXT | User password |

### 🗃️ `decks`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Deck ID |
| `user_id` | INTEGER | Foreign key to `users` |
| `name` | TEXT | Deck name |

### 🧾 `flashcards`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Flashcard ID |
| `deck_id` | INTEGER | Foreign key to `decks` |
| `question` | TEXT | Card question |
| `answer` | TEXT | Card answer |

### 📈 `deck_sessions`
| Column | Type | Description |
|--------|------|-------------|
| `id` | INTEGER PRIMARY KEY AUTOINCREMENT | Session ID |
| `deck_id` | INTEGER | Deck reference |
| `user_id` | INTEGER | User reference |
| `correct_count` | INTEGER | Correct answers |
| `wrong_count` | INTEGER | Incorrect answers |
| `timestamp` | DATETIME | Date/time of the session |

---

## 🧭 How It Works

1. **Login / User creation**  
   A user is stored in the `users` table and their ID is saved in `SharedPreferences`.

2. **Deck listing**  
   The main screen dynamically builds deck cards from the database using `addDeckCard()`.

3. **Flashcard session**  
   Results (correct/wrong) are recorded in `deck_sessions`.

4. **Profile view**  
   Fetches totals from `deck_sessions` and displays:
    - Circle chart for correct/wrong ratio.
    - Weekly bar chart for sessions count.

---

🧑‍💻 Authors

Gabriel — Main developer and designer of the Flashcards App prototype.
🎓 Built as part of a final-year college project.

📜 License

This project is open-source and available under the MIT License.
