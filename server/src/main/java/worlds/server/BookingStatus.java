package worlds.server;

/**
 * PENDING: Realtor has submitted booking and paid
 * MATCHED: Realtor's pending booking matched with appropriate realtor
 * COMPLETED: Media uploaded and confirmed by realtor and content creator
 * CANCELLED: Pending booking cancelled by realtor
 * TENTATIVE: Content creator has uploaded all media and marked the booking as done
 */
enum BookingStatus {
    PENDING, MATCHED, COMPLETED, CANCELLED, TENTATIVE
}