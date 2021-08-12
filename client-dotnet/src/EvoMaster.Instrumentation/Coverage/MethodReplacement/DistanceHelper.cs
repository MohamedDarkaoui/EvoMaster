using System;

public class DistanceHelper
{
    public static readonly double H_REACHED_BUT_NULL = 0.05d;

    public static readonly double H_NOT_NULL = 0.1d;

    public static readonly double H_REACHED_BUT_EMPTY = H_REACHED_BUT_NULL;

    public static readonly double H_NOT_EMPTY = H_NOT_NULL;


    //2^16=65536, max distance for a char
    public static readonly int MAX_CHAR_DISTANCE = 65_536;


    public static double IncreasedDistance(double distance, double delta)
    {
        if (!double.IsFinite(distance) || distance == double.MaxValue)
        {
            return distance;
        }

        if (distance > (double.MaxValue - delta))
        {
            return double.MaxValue;
        }

        return distance + delta;
    }


    public static int DistanceToDigit(char c)
    {
        return DistanceToRange(c, '0', '9');
    }

    public static int DistanceToRange(char c, char minInclusive, char maxInclusive)
    {
        if (minInclusive >= maxInclusive)
        {
            throw new ArgumentException("Invalid char range '" + minInclusive + "'-'" + maxInclusive + "'");
        }

        var diffAfter = minInclusive - c;
        var diffBefore = c - maxInclusive;

        var dist = Math.Max(diffAfter, 0) + Math.Max(diffBefore, 0);

        return dist;
    }

    public static int DistanceToChar(char c, char target)
    {
        return Math.Abs(c - target);
    }


    public static long GetLeftAlignmentDistance(string a, string b)
    {
        long diff = Math.Abs(a.Length - b.Length);
        var dist = diff * MAX_CHAR_DISTANCE;

        for (var i = 0; i < Math.Min(a.Length, b.Length); i++)
        {
            dist += Math.Abs(a[i] - b[i]);
        }

        //assert dist >= 0; TODO
        return dist;
    }

    /**
     * Computes a distance to a==b. If a-b overflows,
     *
     * @param a
     * @param b
     * @return
     */
    public static double GetDistanceToEquality(long a, long b)
    {
        // TODO: Some long values cannot be precisely represented as double values
        return GetDistanceToEquality(a, (double) b);
    }


    public static double GetDistanceToEquality(int a, int b)
    {
        return GetDistanceToEquality(a, (double) b);
    }

    public static double GetDistanceToEquality(char a, char b)
    {
        return GetDistanceToEquality(a, (double) b);
    }

    public static double GetDistanceToEquality(double a, double b)
    {
        if (!double.IsFinite(a) || !double.IsFinite(b))
        {
            // one of the values is not finite
            return double.MaxValue;
        }

        double distance;

        if (a < b)
        {
            distance = b - a;
        }
        else
        {
            distance = a - b;
        }

        if (distance < 0 || !double.IsFinite(distance))
        {
            // overflow has occurred
            return double.MaxValue;
        }

        return distance;
    }

    public static double GetDistanceToEquality(DateTime a, DateTime b)
    {
        if (a.Equals(null)) throw new ArgumentNullException(nameof(a));
        if (b.Equals(null)) throw new ArgumentNullException(nameof(b));

        return DistanceHelper.GetDistanceToEquality(ConvertToTimestamp(a), ConvertToTimestamp(b));
    }

    private static long ConvertToTimestamp(DateTime value)
    {
        var elapsedTime = new DateTimeOffset(value).ToUnixTimeSeconds();
        return elapsedTime;
    }

    public static double GetDistance(object left, object right)
    {
        if (left.Equals(null)) throw new ArgumentNullException(nameof(left));
        if (right.Equals(null)) throw new ArgumentNullException(nameof(right));

        double distance;

        if (left is string && right is string)
        {
            // TODO Add string specialization info for left and right

            // String
            var a = (string) left;
            var b = right.ToString();
            distance = GetLeftAlignmentDistance(a, b);
        }
        else if (left is byte && right is byte)
        {
            // Byte
            var a = (byte) left;
            var b = (byte) right;
            distance = DistanceHelper.GetDistanceToEquality(Convert.ToInt64(a), Convert.ToInt64(b));
        }
        else if (left is short && right is short)
        {
            // Short
            var a = (short) left;
            var b = (short) right;
            distance = DistanceHelper.GetDistanceToEquality(Convert.ToInt64(a), Convert.ToInt64(b));
        }
        else if (left is int && right is int)
        {
            // Integer
            var a = (int) left;
            var b = (int) right;
            distance = GetDistanceToEquality(a, b);
        }
        else if (left is long && right is long)
        {
            // Long
            var a = (long) left;
            var b = (long) right;
            distance = GetDistanceToEquality(a, b);
        }
        else if (left is float && right is float)
        {
            // Float
            var a = (float) left;
            var b = (float) right;
            distance = GetDistanceToEquality(a, b);
        }
        else if (left is double && right is double)
        {
            // Double
            var a = (double) left;
            var b = (double) right;
            distance = GetDistanceToEquality(a, b);
        }
        else if (left is char && right is char)
        {
            // Character
            var a = (char) left;
            var b = (char) right;
            distance = GetDistanceToEquality(a, b);
        }
        else if (left is DateTime && right is DateTime)
        {
            // DateTime
            var a = (DateTime) left;
            var b = (DateTime) right;
            distance = GetDistanceToEquality(a, b);
        }
        else
        {
            distance = double.MaxValue;
        }

        return distance;
    }
}