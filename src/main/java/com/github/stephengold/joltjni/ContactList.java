/*
Copyright (c) 2024 Stephen Gold

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.github.stephengold.joltjni;

/**
 * A variable-length list (array) of character contacts.
 *
 * @author Stephen Gold sgold@sonic.net
 */
public class ContactList extends JoltPhysicsObject {
    // *************************************************************************
    // constructors

    /**
     * Instantiate a list with the specified native object assigned.
     *
     * @param listVa the virtual address of the native object to assign (not
     * zero)
     * @param owner true &rarr; make the JVM object the owner, false &rarr; it
     * isn't the owner
     */
    ContactList(long listVa, boolean owner) {
        Runnable freeingAction = owner ? () -> free(listVa) : null;
        setVirtualAddress(listVa, freeingAction);
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Count how many contacts the currently allocated storage can hold.
     *
     * @return the number of contacts (&ge;size)
     */
    public int capacity() {
        long listVa = va();
        int result = capacity(listVa);
        return result;
    }

    /**
     * Test whether the list contains any elements.
     *
     * @return true if empty, otherwise false
     */
    public boolean empty() {
        if (size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Access the contact at the specified index.
     *
     * @param elementIndex the index from which to get the contact (&ge;0)
     * @return a new JVM object with the pre-existing native object assigned
     */
    public Contact get(int elementIndex) {
        long listVa = va();
        long refVa = get(listVa, elementIndex);
        Contact result = new Contact(refVa, true);

        return result;
    }

    /**
     * Expand or truncate the list.
     *
     * @param numElements the desired size (number of elements)
     */
    public void resize(int numElements) {
        long listVa = va();
        resize(listVa, numElements);
    }

    /**
     * Put the specified contact at the specified index.
     *
     * @param elementIndex the index at which to put the contact (&ge;0)
     * @param contact the contact to put (not null)
     */
    public void set(int elementIndex, Contact contact) {
        long listVa = va();
        long contactVa = contact.va();
        set(listVa, elementIndex, contactVa);
    }

    /**
     * Count how many contacts are in the list.
     *
     * @return the number of contacts (&ge;0, &le;capacity)
     */
    public int size() {
        long listVa = va();
        int result = size(listVa);
        return result;
    }
    // *************************************************************************
    // native private methods

    native private static int capacity(long listVa);

    native private static void free(long listVa);

    native private static long get(long listVa, int elementIndex);

    native private static void resize(long listVa, int numElements);

    native private static void set(
            long listVa, int elementIndex, long contactVa);

    native private static int size(long listVa);
}
