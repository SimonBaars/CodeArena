package com.simonbaars.clonerefactor.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.simonbaars.clonerefactor.settings.Settings;
import com.github.javaparser.TokenRange;

public interface FiltersTokens {
	public static final Category[] NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL};
	public static final Category[] LITERATURE_TYPE2_NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL, Category.IDENTIFIER, Category.LITERAL};
	
	public default Stream<JavaToken> getEffectiveTokens(TokenRange tokens) {
		return StreamSupport.stream(tokens.spliterator(), false).filter(this::isComparableToken);
	}
	
	public default List<JavaToken> getEffectiveTokenList(TokenRange tokens){
		return getEffectiveTokens(tokens).collect(Collectors.toList());
	}
	
	public default boolean isComparableToken(JavaToken t) {
		return Arrays.stream(Settings.get().getCloneType().isNotTypeOne() && Settings.get().useLiteratureTypeDefinitions() ? LITERATURE_TYPE2_NO_TOKEN : NO_TOKEN).noneMatch(c -> c.equals(t.getCategory()));
	}
}
