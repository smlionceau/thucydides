package net.thucydides.core.annotations;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.reports.html.Formatter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.thucydides.core.util.NameConverter.withNoArguments;

/**
 * Utility class used to help process annotations on tests and test steps.
 */
public class TestAnnotations {

    private final Class<?> testClass;

    private TestAnnotations(final Class<?> testClass) {
        this.testClass = testClass;
    }

    public static TestAnnotations forClass(final Class<?> testClass) {
        return new TestAnnotations(testClass);
    }

    public String getAnnotatedTitleForMethod(final String methodName) {
        String annotatedTitle = null;
        if (testClass != null) {
            if (testClassHasMethodCalled(methodName)) {
                annotatedTitle = getAnnotatedTitle(methodName);
            }
        }
        return annotatedTitle;
    }

    public boolean isPending(final String methodName) {
        if (testClass != null) {
            if (testClassHasMethodCalled(methodName)) {
                return isPending(getMethodCalled(methodName));
            }
        }
        return false;
    }

    public static boolean isPending(final Method method) {
        if (method != null) {
            return (method.getAnnotation(Pending.class) != null);
        }
        return false;
    }

    public static boolean isIgnored(final Method method) {
        if (method != null) {
            return hasAnnotationCalled(method, "Ignore");
        }
        return false;
    }

    private static boolean hasAnnotationCalled(Method method, String annotationName) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getSimpleName().equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIgnored(final String methodName) {
        if (testClass != null) {
            return isIgnored(getMethodCalled(methodName));
        }
        return false;
    }

    private String getAnnotatedTitle(String methodName) {
        Method testMethod = getMethodCalled(methodName);
        Title titleAnnotation = testMethod.getAnnotation(Title.class);
        if (titleAnnotation != null) {
            return titleAnnotation.value();
        } else {
            return null;
        }
    }

    private boolean testClassHasMethodCalled(final String methodName) {
        return (getMethodCalled(methodName) != null);

    }

    private Method getMethodCalled(final String methodName) {
        String baseMethodName = withNoArguments(methodName);
        try {
            return testClass.getMethod(baseMethodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Return a list of the issues mentioned in the title annotation of this method.
     * @param methodName
     * @return
     */
    public List<String> getAnnotatedIssuesForMethodTitle(String methodName) {
        String title = getAnnotatedTitleForMethod(methodName);
        if (title != null) {
            return Formatter.issuesIn(title);
        } else {
            return Formatter.issuesIn(methodName);
        }
    }


    private String getAnnotatedIssue(String methodName) {
        Method testMethod = getMethodCalled(methodName);
        if ((testMethod != null) && (testMethod.getAnnotation(Issue.class) != null)){
            return testMethod.getAnnotation(Issue.class).value();
        } else {
            return null;
        }
    }

    private String[] getAnnotatedIssues(String methodName) {
        Method testMethod = getMethodCalled(methodName);
        if ((testMethod != null) && (testMethod.getAnnotation(Issues.class) != null)){
            return testMethod.getAnnotation(Issues.class).value();
        } else {
            return null;
        }
    }

    /**
     * Return a list of the issues mentioned in the Issue annotation of this method.
     */
    public String getAnnotatedIssueForMethod(String methodName) {
        return getAnnotatedIssue(methodName);
    }

    public String[] getAnnotatedIssuesForMethod(String methodName) {
        return getAnnotatedIssues(methodName);
    }

    public String getAnnotatedIssueForTestCase(Class<?> testCase) {
        Issue issueAnnotation = testCase.getAnnotation(Issue.class);
        if (issueAnnotation != null) {
            return issueAnnotation.value();
        } else {
            return null;
        }
    }

    public String[] getAnnotatedIssuesForTestCase(Class<?> testCase) {
        Issues issueAnnotation = testCase.getAnnotation(Issues.class);
        if (issueAnnotation != null) {
            return issueAnnotation.value();
        } else {
            return null;
        }
    }

    public List<String> getIssuesForMethod(String methodName) {
        List<String> issues = new ArrayList<String>();

        if (testClass != null) {
            addIssuesFromMethod(methodName, issues);
        } else {
            addIssuesFromTestScenarioName(methodName, issues);
        }
        return issues;
    }

    private void addIssuesFromTestScenarioName(String methodName, List<String> issues) {
        issues.addAll(getAnnotatedIssuesForMethodTitle(methodName));
    }

    private void addIssuesFromMethod(String methodName, List<String> issues) {
        if (getAnnotatedIssues(methodName) != null) {
            issues.addAll(Arrays.asList(getAnnotatedIssues(methodName)));
        }

        if (getAnnotatedIssue(methodName) != null) {
            issues.add(getAnnotatedIssue(methodName));
        }

        if (getAnnotatedTitle(methodName) != null) {
            addIssuesFromTestScenarioName(methodName, issues);
        }
    }

    public List<WithTag> getTagsForMethod(String methodName) {

        List<WithTag> allTags = new ArrayList<WithTag>(getTags());
        allTags.addAll(getTagsFor(methodName));

        return ImmutableList.copyOf(allTags);
    }

    public List<WithTag> getTags() {
        List<WithTag> tags = new ArrayList<WithTag>();
        addTags(tags, testClass.getAnnotation(WithTags.class));
        addTag(tags, testClass.getAnnotation(WithTag.class));
        return tags;
    }

    private void addTag(List<WithTag> tags, WithTag tag) {
        if (tag != null) {
            tags.add(tag);
        }
    }

    private void addTags(List<WithTag> tags, WithTags tagSet) {
        if (tagSet != null) {
            tags.addAll(Arrays.asList(tagSet.value()));
        }
    }

    public List<WithTag> getTagsFor(String methodName) {
        List<WithTag> tags = new ArrayList<WithTag>();

        Method testMethod = getMethodCalled(methodName);
        if (testMethod != null) {
            addTags(tags, testMethod.getAnnotation(WithTags.class));
            addTag(tags, testMethod.getAnnotation(WithTag.class));
        }
        return tags;
    }

}
