/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.yahoo.yosegi.tools;

import java.util.Map;
import java.util.HashMap;

import jp.co.yahoo.yosegi.binary.maker.*;
import jp.co.yahoo.yosegi.compressor.*;
import jp.co.yahoo.yosegi.spread.column.ColumnType;

public final class StoragePerformanceTarget{

  public static IColumnBinaryMaker[] getColumnBinaryMaker( final ColumnType columnType ){
    switch( columnType ){
      case BOOLEAN:
        return new IColumnBinaryMaker[]{
          new DumpBooleanColumnBinaryMaker(),
        };
      case BYTE:
        return new IColumnBinaryMaker[]{
          new UnsafeOptimizeDumpLongColumnBinaryMaker(),
          new UnsafeOptimizeLongColumnBinaryMaker(),
        };
      case SHORT:
        return new IColumnBinaryMaker[]{
          new UnsafeOptimizeDumpLongColumnBinaryMaker(),
          new UnsafeOptimizeLongColumnBinaryMaker(),
        };
      case INTEGER:
        return new IColumnBinaryMaker[]{
          new UnsafeOptimizeDumpLongColumnBinaryMaker(),
          new UnsafeOptimizeLongColumnBinaryMaker(),
        };
      case LONG:
        return new IColumnBinaryMaker[]{
          new UnsafeOptimizeDumpLongColumnBinaryMaker(),
          new UnsafeOptimizeLongColumnBinaryMaker(),
        };
      case FLOAT:
        return new IColumnBinaryMaker[]{
          new UnsafeRangeDumpFloatColumnBinaryMaker(),
          new UnsafeOptimizeFloatColumnBinaryMaker(),
        };
      case DOUBLE:
        return new IColumnBinaryMaker[]{
          new UnsafeRangeDumpDoubleColumnBinaryMaker(),
          new UnsafeOptimizeDoubleColumnBinaryMaker(),
        };
      case BYTES:
        return new IColumnBinaryMaker[]{
          new DumpBytesColumnBinaryMaker(),
        };
      case STRING:
        return new IColumnBinaryMaker[]{
          new UnsafeOptimizeDumpStringColumnBinaryMaker(),
          new UnsafeOptimizeStringColumnBinaryMaker(),
        };
      default:
        return new IColumnBinaryMaker[0];
    }
  }

  public static ICompressor[] getCompressorClass(){
    return new ICompressor[]{
      new DefaultCompressor(),
      new GzipCompressor(),
      new BZip2CommonsCompressor(),
      new FramedLZ4CommonsCompressor(),
      new SnappyCommonsCompressor(),
      new LzmaCommonsCompressor(),
      new ZstdCommonsCompressor(),
    };
  }

}
