package br.com.lucasrznd.contractmanagementapi.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Generic, accent and case-insensitive "contains" search over a list of fields.
 *
 * <p>The text is split into terms and every term must match at least one field
 * (e.g. "joao londrina" finds a company whose trade name contains "João" and whose city is "Londrina").
 *
 * <p>Fields can be nested through {@code ManyToOne}/{@code OneToOne} associations using dot notation,
 * e.g. {@code "clientCompany.tradeName"}; associations are LEFT JOINed so rows without them are kept.
 *
 * <p>Performance: the generated predicate is {@code immutable_unaccent(lower(field)) LIKE '%term%'}.
 * Each searched field must have a matching trigram index, otherwise PostgreSQL does a sequential scan:
 * <pre>
 * CREATE INDEX ... ON table USING gin (immutable_unaccent(lower(column)) gin_trgm_ops);
 * </pre>
 * Only String fields are supported.
 */
public final class TextSearchSpecification {

    private static final String UNACCENT_FUNCTION = "immutable_unaccent";
    private static final char ESCAPE_CHAR = '\\';
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private TextSearchSpecification() {
    }

    public static <T> Specification<T> of(final String text, final String... fields) {
        if (fields == null || fields.length == 0) {
            throw new IllegalArgumentException("At least one field must be informed for text search");
        }

        return (root, query, cb) -> {
            String[] terms = toTerms(text);
            if (terms.length == 0) {
                return null; // no filter: returns everything
            }

            Expression<?>[] searchableFields = Arrays.stream(fields)
                    .map(field -> normalized(cb, resolvePath(root, field, isCountQuery(query))))
                    .toArray(Expression[]::new);

            Predicate[] termPredicates = Arrays.stream(terms)
                    .map(term -> matchesAnyField(cb, searchableFields, "%" + escapeLike(term) + "%"))
                    .toArray(Predicate[]::new);

            return cb.and(termPredicates);
        };
    }

    @SuppressWarnings("unchecked")
    private static Predicate matchesAnyField(final CriteriaBuilder cb, final Expression<?>[] fields, final String pattern) {
        return cb.or(Arrays.stream(fields)
                .map(field -> cb.like((Expression<String>) field, pattern, ESCAPE_CHAR))
                .toArray(Predicate[]::new));
    }

    private static Expression<String> normalized(final CriteriaBuilder cb, final Path<String> path) {
        return cb.function(UNACCENT_FUNCTION, String.class, cb.lower(path));
    }

    private static boolean isCountQuery(final CriteriaQuery<?> query) {
        return query == null || Long.class.equals(query.getResultType()) || long.class.equals(query.getResultType());
    }

    private static Path<String> resolvePath(final Root<?> root, final String field, final boolean countQuery) {
        String[] parts = field.split("\\.");
        From<?, ?> from = root;

        for (int i = 0; i < parts.length - 1; i++) {
            from = countQuery ? getOrCreateLeftJoin(from, parts[i]) : getOrCreateLeftFetch(from, parts[i]);
        }

        return from.get(parts[parts.length - 1]);
    }

    /**
     * Reuses an existing join to avoid joining the same association once per field/term.
     */
    private static Join<?, ?> getOrCreateLeftJoin(final From<?, ?> from, final String attribute) {
        return from.getJoins().stream()
                .filter(join -> join.getAttribute().getName().equals(attribute) && join.getJoinType() == JoinType.LEFT)
                .findFirst()
                .<Join<?, ?>>map(join -> join)
                .orElseGet(() -> from.join(attribute, JoinType.LEFT));
    }

    /**
     * On the select query the association is fetched instead of only joined: the same join is used to filter
     * and to load the association, so it is not joined twice when the repository also fetches it
     * (e.g. {@code @EntityGraph}), and it does not trigger N+1 selects when it is not.
     * Count queries cannot fetch, so they use a plain join.
     */
    private static Join<?, ?> getOrCreateLeftFetch(final From<?, ?> from, final String attribute) {
        return from.getFetches().stream()
                .filter(fetch -> fetch.getAttribute().getName().equals(attribute) && fetch.getJoinType() == JoinType.LEFT)
                .findFirst()
                .<Join<?, ?>>map(fetch -> (Join<?, ?>) fetch)
                .orElseGet(() -> (Join<?, ?>) from.fetch(attribute, JoinType.LEFT));
    }

    /**
     * Lower case and remove accents on the Java side, mirroring immutable_unaccent(lower(column)) on the database.
     */
    static String[] toTerms(final String text) {
        if (text == null || text.isBlank()) {
            return new String[0];
        }

        String normalized = DIACRITICS.matcher(Normalizer.normalize(text.trim(), Normalizer.Form.NFD)).replaceAll("");
        return WHITESPACE.split(normalized.toLowerCase(Locale.ROOT));
    }

    static String escapeLike(final String term) {
        return term
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

}
