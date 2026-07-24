package com.mediconnect.shared.error;

/**
 * Application-level exceptions mapped to RFC 7807 responses by
 * {@link GlobalExceptionHandler}. Keeping them together documents the full set
 * of failure modes the API can surface.
 */
public final class DomainExceptions {

    private DomainExceptions() {
    }

    /** 404 — a referenced resource does not exist (or is not visible to this tenant). */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /** 409 — the request conflicts with current state (e.g. double booking, duplicate). */
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    /** 422 — the request is well-formed but violates a business rule. */
    public static class BusinessRuleException extends RuntimeException {
        private final String code;

        public BusinessRuleException(String code, String message) {
            super(message);
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    /**
     * 404 — a cross-tenant access attempt. Deliberately surfaced as "not found"
     * so that object existence is never disclosed across clinic boundaries.
     */
    public static class TenantViolationException extends RuntimeException {
        public TenantViolationException(String message) {
            super(message);
        }
    }

    /** 403 — the caller is authenticated but not permitted to perform the action. */
    public static class ForbiddenOperationException extends RuntimeException {
        public ForbiddenOperationException(String message) {
            super(message);
        }
    }
}
