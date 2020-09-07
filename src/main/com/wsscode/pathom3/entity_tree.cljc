(ns com.wsscode.pathom3.entity-tree
  (:require
    [clojure.spec.alpha :as s]
    [com.fulcrologic.guardrails.core :refer [<- => >def >defn >fdef ? |]]
    [com.wsscode.misc.core :as misc]))

(>def ::entity-tree map?)
(>def ::entity-tree* misc/atom?)

(>defn entity
  "Returns the entity tree value from env"
  [{::keys [entity-tree*]}]
  [(s/keys :opt [::entity-tree*]) => (? map?)]
  (some-> entity-tree* deref))

(>defn with-entity
  "Set the entity in the environment. Note in this function you must send the cache-tree
  as a map, not as an atom."
  [env entity-tree]
  [map? map? => map?]
  (assoc env ::entity-tree* (atom entity-tree)))

(>defn swap-entity!
  "Swap cache-tree at the current path. Returns the updated whole cache-tree."
  ([{::keys [entity-tree*]} f]
   [(s/keys :req [::entity-tree*]) fn?
    => map?]
   (swap! entity-tree* f))
  ([{::keys [entity-tree*]} f x]
   [(s/keys :req [::entity-tree*]) fn? any?
    => map?]
   (swap! entity-tree* f x))
  ([{::keys [entity-tree*]} f x y]
   [(s/keys :req [::entity-tree*]) fn? any? any?
    => map?]
   (swap! entity-tree* f x y))
  ([{::keys [entity-tree*]} f x y & args]
   [(s/keys :req [::entity-tree*]) fn? any? any? (s/+ any?)
    => map?]
   (apply swap! entity-tree* f x y args)))

(>defn merge-entity-data
  "Specialized merge versions that work on entity data."
  [entity new-data]
  [::entity-tree ::entity-tree => ::entity-tree]
  (reduce-kv
    assoc
    entity
    new-data))
