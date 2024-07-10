package team03.mudecowboys.projekt2.jcleaner.model.util;

import java.lang.reflect.Member;


/**
 * Record which contains a class and its member.
 * This is used when a particular member in a class does not match the predefined clean code guidelines.
 */
public record CodePositionWarning(Class<?> classObj, Member memberObj, String... informations) {}