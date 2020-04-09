package worlds.server;

/**
 * PENDING: A user has not yet been reviewed by an Admin. This user cannot access the 
 *          platform until they have been marked as “APPROVED”
 * APPROVED: A user that has been reviewed by an Admin. This user can access the platform.
 * BANNED: A user that has been restricted from accessing the platform.
 * DELETED: A user that has been deleted by an Admin.
 */
enum UserStatus {
    PENDING, APPROVED, BANNED, DELETED
}