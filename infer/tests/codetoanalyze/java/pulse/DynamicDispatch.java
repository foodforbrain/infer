/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package codetoanalyze.java.infer;

public class DynamicDispatch {

  static interface Interface {
    public Object foo();
  }

  static class Impl implements Interface {
    @Override
    public Object foo() {
      return null;
    }
  }

  static void interfaceShouldNotCauseFalseNegativeEasyBad() {
    Interface i = new Impl();
    // should be a warning since Impl's implementation of foo returns null
    i.foo().toString();
  }

  static void FN_interfaceShouldNotCauseFalseNegativeHardOK(Interface i) {
    i.foo().toString();
  }

  static void callWithBadImplementationBad_FN(Impl impl) {
    FN_interfaceShouldNotCauseFalseNegativeHardOK(impl);
  }

  static class Supertype {
    Object foo() {
      return new Object();
    }

    Object bar() {
      return null;
    }
  }

  static class Subtype extends Supertype {
    @Override
    Object foo() {
      return null;
    }

    @Override
    Object bar() {
      return new Object();
    }
  }

  static void dynamicDispatchShouldNotCauseFalseNegativeEasyBad() {
    Supertype o = new Subtype();
    // should report a warning because we know the dynamic type of o is Subtype
    o.foo().toString();
  }

  static void dynamicDispatchShouldNotCauseFalsePositiveEasyOK() {
    Supertype o = new Subtype();
    // should not report a warning because we know the dynamic type of o is Subtype
    o.bar().toString();
  }

  static void dynamicDispatchShouldNotReportWhenCallingSupertypeOK(Supertype o) {
    // should not report a warning because the Supertype implementation
    // of foo() does not return null
    o.foo().toString();
  }

  static void dynamicDispatchShouldReportWhenCalledWithSubtypeParameterBad_FN(Subtype o) {
    // should report a warning because the Subtype implementation
    // of foo() returns null
    dynamicDispatchShouldNotReportWhenCallingSupertypeOK(o);
  }

  static Object dynamicDispatchWrapperFoo(Supertype o) {
    return o.foo();
  }

  static Object dynamicDispatchWrapperBar(Supertype o) {
    return o.bar();
  }

  static void dynamicDispatchCallsWrapperWithSupertypeOK() {
    // Should not report because Supertype.foo() does not return null
    Supertype o = new Supertype();
    dynamicDispatchWrapperFoo(o).toString();
  }

  static void dynamicDispatchCallsWrapperWithSupertypeBad() {
    // Should report because Supertype.bar() returns null
    Supertype o = new Supertype();
    dynamicDispatchWrapperBar(o).toString();
  }

  static void dynamicDispatchCallsWrapperWithSubtypeBad() {
    // Should report because Subtype.foo() returns null
    Supertype o = new Subtype();
    dynamicDispatchWrapperFoo(o).toString();
  }

  static void dynamicDispatchCallsWrapperWithSubtypeOK() {
    // Should not report because Subtype.bar() does not returns null
    Supertype o = new Subtype();
    dynamicDispatchWrapperBar(o).toString();
  }

  static class WithField {

    Supertype mField;

    WithField(Supertype t) {
      mField = t;
    }

    static void dispatchOnFieldOK() {
      Supertype subtype = new Subtype();
      WithField object = new WithField(subtype);
      object.mField.bar().toString();
    }

    static void dispatchOnFieldBad() {
      Supertype subtype = new Subtype();
      WithField object = new WithField(subtype);
      object.mField.foo().toString();
    }
  }

  private Object callFoo(Supertype o) {
    return o.foo();
  }

  void dynamicResolutionWithPrivateMethodBad() {
    Supertype subtype = new Subtype();
    callFoo(subtype).toString();
  }

  Object variadicMethod(Supertype... args) {
    if (args.length == 0) {
      return null;
    } else {
      return args[0].foo();
    }
  }

  void dynamicResolutionWithVariadicMethodBad() {
    Supertype subtype = new Subtype();
    variadicMethod(subtype, null, null).toString();
  }
}

class InheritanceDispatch {
  class A {
    int foo() {
      return 32;
    }
  }

  class B extends A {
    int foo() {
      return 52;
    }
  }

  class C extends B {}

  A getB() {
    return new B();
  }

  A getC() {
    return new C();
  }

  void dispatch_to_B_ok() {
    A b = getB();
    if (b.foo() == 32) {
      Object o = null;
      o.toString();
    }
  }

  void dispatch_to_B_bad() {
    A b = getB();
    if (b.foo() == 52) {
      Object o = null;
      o.toString();
    }
  }

  void dispatch_to_A_bad() {
    A a = new A();
    if (a.foo() == 32) {
      Object o = null;
      o.toString();
    }
  }

  void dispatch_to_C_bad() {
    A c = getC();
    if (c.foo() == 52) {
      Object o = null;
      o.toString();
    }
  }
}
