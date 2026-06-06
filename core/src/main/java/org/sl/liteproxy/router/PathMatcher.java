package org.sl.liteproxy.router;

import java.util.regex.Pattern;

public sealed interface PathMatcher permits PathMatcher.ExactPath, PathMatcher.RegexPath, PathMatcher.PrefixPath {

    boolean match(String path);
     static PathMatcher exact(String path){
         return new ExactPath(path);
     }

    static PathMatcher prefix(String path){
        return new PrefixPath(path);
    }
    static PathMatcher regex(String path){
        return new RegexPath(path);
    }


    record ExactPath(String path) implements PathMatcher {
        @Override
        public boolean match(String path) {
            return this.path.equals(path);
        }
    }

    record PrefixPath(String path) implements PathMatcher {
        @Override
        public boolean match(String path) {
            return path != null && path.startsWith(this.path);
        }
    }

    record RegexPath(String regex) implements PathMatcher {

        @Override
        public boolean match(String path) {
            Pattern pattern = Pattern.compile(this.regex);
            return pattern.matcher(path).matches();
        }
    }
}