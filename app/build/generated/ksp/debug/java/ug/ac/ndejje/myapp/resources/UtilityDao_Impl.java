package ug.ac.ndejje.myapp.resources;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UtilityDao_Impl implements UtilityDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UtilityEntity> __insertionAdapterOfUtilityEntity;

  private final EntityDeletionOrUpdateAdapter<UtilityEntity> __deletionAdapterOfUtilityEntity;

  private final EntityDeletionOrUpdateAdapter<UtilityEntity> __updateAdapterOfUtilityEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UtilityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUtilityEntity = new EntityInsertionAdapter<UtilityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `utilities` (`id`,`userId`,`name`,`provider`,`accountNumber`,`defaultAmount`,`categoryId`,`modeId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UtilityEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getProvider());
        if (entity.getAccountNumber() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getAccountNumber());
        }
        statement.bindDouble(6, entity.getDefaultAmount());
        statement.bindLong(7, entity.getCategoryId());
        statement.bindLong(8, entity.getModeId());
      }
    };
    this.__deletionAdapterOfUtilityEntity = new EntityDeletionOrUpdateAdapter<UtilityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `utilities` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UtilityEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfUtilityEntity = new EntityDeletionOrUpdateAdapter<UtilityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `utilities` SET `id` = ?,`userId` = ?,`name` = ?,`provider` = ?,`accountNumber` = ?,`defaultAmount` = ?,`categoryId` = ?,`modeId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UtilityEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getProvider());
        if (entity.getAccountNumber() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getAccountNumber());
        }
        statement.bindDouble(6, entity.getDefaultAmount());
        statement.bindLong(7, entity.getCategoryId());
        statement.bindLong(8, entity.getModeId());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM utilities WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final UtilityEntity utility, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUtilityEntity.insert(utility);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final UtilityEntity utility, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfUtilityEntity.handle(utility);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UtilityEntity utility, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUtilityEntity.handle(utility);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final int userId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<UtilityEntity>> getAllUtilities(final int userId) {
    final String _sql = "SELECT * FROM utilities WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"utilities"}, new Callable<List<UtilityEntity>>() {
      @Override
      @NonNull
      public List<UtilityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfProvider = CursorUtil.getColumnIndexOrThrow(_cursor, "provider");
          final int _cursorIndexOfAccountNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "accountNumber");
          final int _cursorIndexOfDefaultAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "defaultAmount");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfModeId = CursorUtil.getColumnIndexOrThrow(_cursor, "modeId");
          final List<UtilityEntity> _result = new ArrayList<UtilityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UtilityEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpProvider;
            _tmpProvider = _cursor.getString(_cursorIndexOfProvider);
            final String _tmpAccountNumber;
            if (_cursor.isNull(_cursorIndexOfAccountNumber)) {
              _tmpAccountNumber = null;
            } else {
              _tmpAccountNumber = _cursor.getString(_cursorIndexOfAccountNumber);
            }
            final double _tmpDefaultAmount;
            _tmpDefaultAmount = _cursor.getDouble(_cursorIndexOfDefaultAmount);
            final int _tmpCategoryId;
            _tmpCategoryId = _cursor.getInt(_cursorIndexOfCategoryId);
            final int _tmpModeId;
            _tmpModeId = _cursor.getInt(_cursorIndexOfModeId);
            _item = new UtilityEntity(_tmpId,_tmpUserId,_tmpName,_tmpProvider,_tmpAccountNumber,_tmpDefaultAmount,_tmpCategoryId,_tmpModeId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
