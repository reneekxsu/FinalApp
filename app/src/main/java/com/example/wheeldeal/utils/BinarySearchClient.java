package com.example.wheeldeal.utils;

/**
 * @brief Java program to implement Binary Search for strings
 * Citation: GeeksforGeeks
 */
public class BinarySearchClient {
    public BinarySearchClient(){};

    /**
     * @brief Searches through an array of strings using binary search to see if a specified string
     *        is in the array
     * @param arr Array of strings, which must be alphabetically sorted
     * @param match The string we want to check if it exists in the array
     * @return Returns index of the searched string if it is present in the string array
     *         If not found, returns -1
     */
    public int binarySearch(String[] arr, String match)
    {
        int leftBound = 0, rightBound = arr.length - 1;
        while (leftBound <= rightBound) {
            int mid = leftBound + (rightBound - leftBound) / 2;
            int res = match.compareTo(arr[mid]);
            // Check if match is present at mid
            if (res == 0)
                return mid;
            // If match greater, ignore left half
            if (res > 0)
                leftBound = mid + 1;
            // If match is smaller, ignore right half
            else
                rightBound = mid - 1;
        }
        return -1;
    }

}
