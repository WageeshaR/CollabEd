package com.collabed.core.service.intel.criteria;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Criteria {
    private final CriteriaScope scope;
    private final CriteriaTarget input;
    private final CriteriaTarget subject;
    private final CriteriaTarget output;

    public static CriteriaBuilder filter() {
        return new CriteriaBuilder(CriteriaScope.FILTER);
    }

    public static CriteriaBuilder generate() {
        return new CriteriaBuilder(CriteriaScope.GENERATE);
    }

    public static CriteriaBuilder query() {
        return new CriteriaBuilder(CriteriaScope.QUERY);
    }

    public static CriteriaBuilder similarity() {
        return new CriteriaBuilder(CriteriaScope.SIMILARITY);
    }

    public static CriteriaBuilder proximity() {
        return new CriteriaBuilder(CriteriaScope.PROXIMITY);
    }

    public static class CriteriaBuilder {
        private CriteriaScope scope;
        private CriteriaTarget input;
        private CriteriaTarget subject;
        private CriteriaTarget output;
        CriteriaBuilder(CriteriaScope scope) {
            this.scope = scope;
        }

        public void input(CriteriaTarget target) {
            this.input = target;
        }

        public boolean hasInput() {
            return this.input != null;
        }

        public void subject(CriteriaTarget target) {
            this.subject = target;
        }

        public boolean hasSubject() {
            return this.subject != null;
        }

        public void output(CriteriaTarget target) {
            this.output = target;
        }

        public boolean hasOutput() {
            return this.output != null;
        }

        public Criteria build() {
            return new Criteria(this.scope, this.input, this.subject, this.output);
        }
    }
}
