package com.gwozdz1uu.hibernate_mastery;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.gwozdz1uu.hibernate_mastery");

    @Test
    void servicesShouldNotDependOnEntityManager() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat().haveNameMatching(".*EntityManager.*");

        rule.check(classes);
    }

    @Test
    void facadeShouldNotDependOnDaoLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..facade..")
                .should().dependOnClassesThat().resideInAPackage("..dao..");

        rule.check(classes);
    }
}
