package worlds.server;

/**
 * UserState represents status of user.
 * UNLOCKED: Account is free to log in/log out.
 * LOCKED: Failed to log in max_attempts number of times (5 or more), therefore user is locked out.
 */
enum UserState {
    UNLOCKED, LOCKED
}