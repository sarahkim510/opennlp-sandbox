package org.apache.opennlp.tf.guillaumegenthial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Viterbi {

  /*
  """Viterbi the highest scoring sequence of tags outside of TensorFlow.
  This should only be used at test time.
  Args:
    score: A [seq_len, num_tags] matrix of unary potentials.
    transition_params: A [num_tags, num_tags] matrix of binary potentials.
  Returns:
    viterbi: A [seq_len] list of integers containing the highest scoring tag
        indices.
    viterbi_score: A float containing the score for the Viterbi sequence.
  """
   */

  private static float[][] zeros_like(float[][] matrix) {
    float[][] returnValue = new float[matrix.length][matrix[0].length];
    for (int i=0; i<matrix.length; i++)
      Arrays.fill(returnValue[i], 0.0f);
    return returnValue;
  }

  private static int[][] zeros_like(int[] shape) {
    int[][] returnValue = new int[shape[0]][shape[1]];
    for (int i=0; i<shape[0]; i++)
      Arrays.fill(returnValue[i], 0);
    return returnValue;
  }

  private static int[] shape(float[][] var) {
    return new int[] {var.length, var[0].length};
  }

  private static float[][] expand_dims_axis_one_plus_array(float[] array, float[][] plus) {
    int[] plus_shape = shape(plus);
    if (plus_shape[0] != array.length)
      throw new RuntimeException("Not same shape");
    float[][] returnValue = new float[plus_shape[0]][plus_shape[1]];
    for (int i=0; i < array.length; i++) {
      for (int j=0; j < plus_shape[1]; j++) {
        returnValue[i][j] = array[i] + plus[i][j];
      }
    }
    return returnValue;
  }

  private static float[] max_columnwise(float[][] array) {
    float[] returnValue = new float[array[0].length];
    for (int col=0; col < array[0].length; col++) {
      returnValue[col] = Float.MIN_VALUE;
      for (int row=0; row < array.length; row++) {
        returnValue[col] = Float.max(returnValue[col],array[row][col]);
      }
    }

    return returnValue;
  }

  private static float max(float[] array) {
    float returnValue = Float.MIN_VALUE;
    for (int col=0; col < array.length; col++) {
        returnValue = Float.max(returnValue, array[col]);
    }
    return returnValue;
  }

  private static int[] argmax_columnwise(float[][] array) {
    int[] returnValue = new int[array[0].length];
    for (int col=0; col < array[0].length; col++) {
      float max = Float.MIN_VALUE;
      int idx = -1;
      for (int row=0; row < array.length; row++) {
        if (Float.compare(array[row][col], max) > 0) {
          max = array[row][col];
          idx = row;
        }
      }
      returnValue[col] = idx;
    }
    return returnValue;
  }

  private static int argmax(float[] array) {
    int returnValue = -1;
    float max = Float.MIN_VALUE;
    for (int col=0; col < array.length; col++) {
      if (Float.compare(array[col], max) > 0) {
        max = array[col];
        returnValue = col;
      }
    }
    return returnValue;
  }

  public static float[] plus(float[] a, float[] b) {
    if (a.length == b.length) {
      float[] returnValue = new float[a.length];
      for(int i = 0; i < a.length; ++i) {
        returnValue[i] = Float.sum(a[i], b[i]);
      }
      return returnValue;
    } else {
      throw new IllegalArgumentException("Arrays doesn't have same shape.");
    }
  }

  public static List<Integer> decode(float[][] score, float[][] transition_params) {
    // trellis = np.zeros_like(score)
    float[][] trellis = zeros_like(score);

    // backpointers = np.zeros_like(score, dtype=np.int32)
    int[][] backpointers = zeros_like(shape(score));

    // trellis[0] = score[0]
    trellis[0] = score[0];

    // for t in range(1, score.shape[0]):
    for (int t=1; t < score.length; t++) {
      //v = np.expand_dims(trellis[t - 1], 1) + transition_params
      float[][] v = expand_dims_axis_one_plus_array(trellis[t - 1], transition_params);

      //trellis[t] = score[t] + np.max(v, 0)
      trellis[t] = plus(score[t], max_columnwise(v));

      //backpointers[t] = np.argmax(v, 0)
      backpointers[t] = argmax_columnwise(v);
    }

    // viterbi = [np.argmax(trellis[-1])]
    List<Integer> viterbi = new ArrayList();
    viterbi.add(argmax(trellis[trellis.length - 1]));

    // for bp in reversed(backpointers[1:]):
    for (int i=backpointers.length - 1; i >= 1; i--) {
      // viterbi.append(bp[viterbi[-1]])
      int[] bp = backpointers[i];
      viterbi.add(bp[viterbi.get(viterbi.size() - 1)]);
    }

    // viterbi.reverse()
    Collections.reverse(viterbi);

    // viterbi_score = np.max(trellis[-1])
    // float viterbi_score = max(trellis[trellis.length - 1])) not used!

    // return viterbi, viterbi_score
    return viterbi;
  }

}