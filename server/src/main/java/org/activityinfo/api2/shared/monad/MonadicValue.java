package org.activityinfo.api2.shared.monad;


import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * Marker interface for MonadicValues.
 *
 * <p>Monads are a concept that we're borrowing from functional programming to deal cleanly with
 * asynchronous operations in the browser. Without some sort of functional approach, asynchronous
 * operations, whether those are AJAX requests, indexedb queries, quickly devolve into callback
 * pyramids of doom, and lead to terrible, awful, unmaintainable code that no one wants to touch.</p>
 *
 * <p>{@code Promise}s, which we're stealing from the JavaScript world, are the most important Monads for
 * ActivityInfo. Promises wrap a given type {@code T} along with the {@code Promise.State}
 * of the underlying value: it can be {@code PENDING}, meaning that we're waiting for the server or the database
 * thread to respond, or it could be {@code REJECTED}, meaning that some error has occurred and we won't get the value,
 * or {@code SETTLED}, meaning the value is available.
 * </p>
 *
 * <p>The {@link Optional} class from the Guava library is another example of a Monad: it wraps an underlying
 * type {@code T} along with, essentially, a flag indicating whether the value is present or absent.  </p>
 *
 * <p>The challenge of dealing with Monads in general, and Promises specifically, are to separate core
 * program logic from the bookkeeping needed for dealing the details related to the Monadic Class. For
 * the {@code Optional} monad, the goal is to separate whatever interesting stuff you're doing without having
 * to check for {@code null} on every line.</p>
 *
 * <p>For the case of our Promises, we want to get about our business without having to constantly worry about
 * whether the request succeeded or failed. Or rather, we want to group together a series of operations, perhaps
 * involving many many asynchronous requests, and only deal once with the question of whether or not the operation
 * succeeded as a whole.</p>
 *
 * <p>We can achieve this separation of concerns with the what Functional Programming People
 * call the <strong>bind</strong> operator, or what we've modeled as a method on the MonadicValue called
 * {@link #then(com.google.common.base.Function)} to match the JavaScript promise specification.
 * </p>
 *
 *
 * @see {@linkplain}
 * @author Alex Bertram
 */
public interface MonadicValue<T> {


    /**
     * <strong>Binds</strong> this monadic value to the given function.
     *
     * <p>Typically, the binding operation can be understood as having four stages:</p>
     * <ol>
     * <li>The monad-related structure on the first argument is "pierced" to expose any number of
     *   values in the underlying type t.</li>
     * <li>The given function is applied to all of those values to obtain values of type (M u).</li>
     * <li>The monad-related structure on those values is also pierced, exposing values of type u.</li>
     * <li>Finally, the monad-related structure is reassembled over all of the results, giving a
     *   single value of type (M u).</li>
     * </ol>
     *
     * @param function the function to be bound to this MonadicValue
     * @param <R> The type of the function's return value
     * @return A new MonadicValue encapsulating the result of the {@code function}'s application
     * and having the same MonadicClass as this MonadicValue
     */
     <R> MonadicValue<R> then(Function<? super T, R> function);




}