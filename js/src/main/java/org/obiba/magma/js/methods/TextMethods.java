package org.obiba.magma.js.methods;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RegExpProxy;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.obiba.magma.js.ScriptableValue;
import org.obiba.magma.type.BooleanType;
import org.obiba.magma.type.TextType;

/**
 * Methods of the {@code ScriptableValue} javascript class that returns {@code ScriptableValue} of {@code BooleanType}
 */
public class TextMethods {

  /**
   * <pre>
   *   $('TextVar').trim()
   * </pre>
   */
  public static ScriptableValue trim(Context ctx, Scriptable thisObj, Object[] args, Function funObj) {
    ScriptableValue sv = (ScriptableValue) thisObj;
    if(sv.getValue().isNull()) {
      return new ScriptableValue(thisObj, TextType.get().nullValue());
    }
    String stringValue = sv.getValue().toString();
    return new ScriptableValue(thisObj, TextType.get().valueOf(stringValue.trim()));
  }

  /**
   * <pre>
   *   $('TextVar').replace('regex', '$1')
   * </pre>
   * @see https://developer.mozilla.org/en/Core_JavaScript_1.5_Reference/Global_Objects/String/replace
   */
  public static ScriptableValue replace(Context ctx, Scriptable thisObj, Object[] args, Function funObj) {
    ScriptableValue sv = (ScriptableValue) thisObj;
    if(sv.getValue().isNull()) {
      return new ScriptableValue(thisObj, TextType.get().nullValue());
    }

    String stringValue = sv.getValue().toString();

    // Delegate to Javascript's String.replace method
    String result = (String) ScriptRuntime.checkRegExpProxy(ctx).action(ctx, thisObj, ScriptRuntime.toObject(thisObj, stringValue), args, RegExpProxy.RA_REPLACE);

    return new ScriptableValue(thisObj, TextType.get().valueOf(result));
  }

  /**
   * <pre>
   *   $('TextVar').matches('regex1', 'regex2', ...)
   * </pre>
   */
  public static ScriptableValue matches(Context ctx, Scriptable thisObj, Object[] args, Function funObj) {
    ScriptableValue sv = (ScriptableValue) thisObj;
    if(sv.getValue().isNull()) {
      return new ScriptableValue(thisObj, TextType.get().nullValue());
    }

    String stringValue = sv.getValue().toString();

    // Delegate to Javascript's String.replace method
    boolean matches = false;
    for(Object arg : args) {
      Object result = ScriptRuntime.checkRegExpProxy(ctx).action(ctx, thisObj, ScriptRuntime.toObject(thisObj, stringValue), new Object[] { arg }, RegExpProxy.RA_MATCH);
      if(result != null) {
        matches = true;
      }
    }

    return new ScriptableValue(thisObj, BooleanType.get().valueOf(matches));
  }

  /**
   * Returns a new {@link ScriptableValue} of {@link TextType} combining the String value of this value with the String
   * values of the parameters parameters.
   * 
   * <pre>
   *   $('TextVar').concat($('TextVar'))
   *   $('Var').concat($('Var'))
   *   $('Var').concat('SomeValue')
   * </pre>
   */
  public static ScriptableValue concat(Context ctx, Scriptable thisObj, Object[] args, Function funObj) {
    ScriptableValue sv = (ScriptableValue) thisObj;

    StringBuilder sb = new StringBuilder();

    sb.append(sv.toString());
    if(args != null) {
      for(Object arg : args) {
        if(arg instanceof ScriptableValue) {
          arg = arg.toString();
        }
        sb.append(arg);
      }
    }
    return new ScriptableValue(thisObj, TextType.get().valueOf(sb.toString()));
  }
}
