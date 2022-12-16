package main.java.model.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Util class to hold all the methods and variables that are common to many classes.
 */
public class Util {
  public static final String FILE_SEPARATOR = "\\|";
  public static final String FILE_OUTPUT_SEPARATOR = "|";
  public static Gson IN_GSON =
          new GsonBuilder().registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type typeOfT,
                                         JsonDeserializationContext context)
                    throws JsonParseException {
              return getDateFromString(json.getAsString());
            }
          }).create();
  public static Gson OUT_GSON = new GsonBuilder().setPrettyPrinting()
          .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public JsonElement serialize(LocalDate localDate, Type type,
                            JsonSerializationContext jsonSerializationContext) {
              return new JsonPrimitive(
                              localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
          }).create();

  /**
   * A generic method to convert the given string into a LocalDate object.
   *
   * @param stringDate date in the string format
   * @return LocalDate object after parsing the given string.
   * @throws DateTimeParseException If the incoming string is not of the form yyyy-mm-dd or some
   *                                invalid input string is passed to the method
   */
  public static LocalDate getDateFromString(String stringDate) throws DateTimeParseException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(stringDate, formatter.withResolverStyle(ResolverStyle.SMART));
  }

  /**
   * A Utility method to avoid nullPointer exception while operating on streams.
   *
   * @param collection that needs to be converted to stream
   * @param <T>        DataType of the Stream/collection
   * @return empty stream if collection passed to it is null else returns the stream conversion of
   *         the valid non-null collection.
   */
  public static <T> Stream<T> collectionToParallelStream(Collection<T> collection) {
    return Optional.ofNullable(collection).map(Collection::parallelStream).orElseGet(Stream::empty);
  }

  /**
   * Enum to represent the type of transactions supported in the application.
   */
  public enum TransactionType {
    BUY, SELL
  }
}
