(*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *)

open! IStd

module BoundsOfNSCollection : sig
  val linear_length : Exp.t -> of_function:string -> CostUtils.model
  (** Return the length of [NSCollection] as a cost value *)
end

module Call : sig
  val dispatch : (Tenv.t, CostUtils.model, unit) ProcnameDispatcher.Call.dispatcher
end
