package com.mediconnect.shared.error;

import com.mediconnect.shared.error.DomainExceptions.BusinessRuleException;
import com.mediconnect.shared.error.DomainExceptions.ConflictException;
import com.mediconnect.shared.error.DomainExceptions.ForbiddenOperationException;
import com.mediconnect.shared.error.DomainExceptions.ResourceNotFoundException;
import com.mediconnect.shared.error.DomainExceptions.TenantViolationException;
import com.mediconnect.shared.web.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates exceptions into RFC 7807 {@code application/problem+json} responses
 * with a stable {@code type} URI, a machine-readable error code and the request
 * correlation id, without ever leaking stack traces or sensitive detail.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TYPE_BASE = "https://mediconnect.example/problems/";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return problem(HttpStatus.NOT_FOUND, "not-found", "Resource not found", ex.getMessage(), req);
    }

    @ExceptionHandler(TenantViolationException.class)
    public ProblemDetail handleTenantViolation(TenantViolationException ex, HttpServletRequest req) {
        // Logged at WARN for the audit trail; response is an indistinguishable 404.
        log.warn("Tenant boundary violation: {}", ex.getMessage());
        return problem(HttpStatus.NOT_FOUND, "not-found", "Resource not found", "Resource not found", req);
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "conflict", "Conflict", ex.getMessage(), req);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(OptimisticLockingFailureException ex, HttpServletRequest req) {
        return problem(HttpStatus.CONFLICT, "concurrent-modification",
                "Concurrent modification",
                "The resource was modified concurrently. Reload and retry.", req);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.UNPROCESSABLE_ENTITY, "business-rule",
                "Business rule violated", ex.getMessage(), req);
        pd.setProperty("code", ex.getCode());
        return pd;
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbiddenOperation(ForbiddenOperationException ex, HttpServletRequest req) {
        return problem(HttpStatus.FORBIDDEN, "forbidden", "Forbidden", ex.getMessage(), req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return problem(HttpStatus.FORBIDDEN, "forbidden", "Forbidden",
                "You do not have permission to perform this action.", req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return problem(HttpStatus.UNAUTHORIZED, "unauthorized", "Unauthorized",
                "Authentication is required or has failed.", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "validation",
                "Validation failed", "One or more fields are invalid.", req);
        List<Object> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.add(java.util.Map.of("field", fe.getField(), "message",
                    fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()));
        }
        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return problem(HttpStatus.BAD_REQUEST, "malformed-request", "Malformed request",
                "The request body could not be read.", req);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest req) {
        // Never expose internal detail; log with correlation id for diagnosis.
        log.error("Unhandled exception for {} {}", req.getMethod(), req.getRequestURI(), ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "Internal server error",
                "An unexpected error occurred.", req);
    }

    private ProblemDetail problem(HttpStatus status, String type, String title,
                                  String detail, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create(TYPE_BASE + type));
        pd.setInstance(URI.create(req.getRequestURI()));
        pd.setProperty("timestamp", Instant.now().toString());
        Object correlationId = req.getAttribute(CorrelationIdFilter.ATTRIBUTE);
        if (correlationId != null) {
            pd.setProperty("correlationId", correlationId);
        }
        return pd;
    }
}
