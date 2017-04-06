package com.nador.mobilemed.data.dagger.scopes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by nador on 06/07/16.
 */
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationScope {
}
