package com.dozingcatsoftware.vectorpinball.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import java.io.IOException;
import java.util.Arrays;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

public class GroovyFieldDelegateBuilder {

    public static GroovyFieldDelegate createFromScript(String script, ClassLoader classLoader) {
        CompilerConfiguration config = new CompilerConfiguration();

        SecureASTCustomizer secureAst = new SecureASTCustomizer();
        secureAst.setIndirectImportCheckEnabled(true);
        // TODO: Determine which classes should be accessible.
        secureAst.setImportsBlacklist(Arrays.asList("java.lang.Process"));
        secureAst.setReceiversClassesBlackList(Arrays.asList(String.class));

        config.addCompilationCustomizers(secureAst);

        try (GroovyClassLoader gcl = new GroovyClassLoader(classLoader, config)) {
            @SuppressWarnings("unchecked")
            Class<? extends Script> groovyScriptClass = gcl.parseClass(script);
            return (new GroovyFieldDelegate()).initWithScript(groovyScriptClass.newInstance());
        }
        catch (IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
