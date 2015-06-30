package domain

import com.sun.istack.internal.Nullable
import org.codehaus.jackson.annotate.JsonProperty

class User {
    int id
    @JsonProperty(value = "first_name")String firstName
    @Nullable
    @JsonProperty(value = "last_name")
    String lastName
    @Nullable
    String username


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
