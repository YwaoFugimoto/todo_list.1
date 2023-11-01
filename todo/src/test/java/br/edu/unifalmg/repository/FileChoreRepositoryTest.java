package br.edu.unifalmg.repository;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileChoreRepositoryTest {

    @InjectMocks
    private FileChoreRepository repository;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#load > When the file is found > When the content is empty > Return empty list")
    void loadWhenTheFileIsFoundWhenTheContentIsEmptyReturnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(MismatchedInputException.class);

        List<Chore> response = repository.load();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is not found (or path is invalid) > Return an empty list")
    void loadWhenTheFileIsNotFoundOrPathIsInvalidReturnAnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenThrow(FileNotFoundException.class);

        List<Chore> response = repository.load();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is loaded > Return a chores' list")
    void loadWhenTheFileIsLoadedReturnAChoresList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("chores.json"), Chore[].class)
        ).thenReturn(new Chore[] {
                new Chore("First Chore", Boolean.FALSE, LocalDate.now()),
                new Chore("Second Chore", Boolean.TRUE, LocalDate.now().minusDays(5))
        });

        List<Chore> chores = repository.load();
        assertAll(
                () -> assertEquals(2, chores.size()),
                () -> assertEquals("First Chore", chores.get(0).getDescription()),
                () -> assertEquals(LocalDate.now().minusDays(5), chores.get(1).getDeadline())
        );
    }

    @Test
    @DisplayName("#save > When unable to write the chores on the file > Return false")
    void saveWhenUnableToWriteTheChoresOnTheFileReturnFalse() throws IOException {
        Mockito.doThrow(IOException.class)
                .when(mapper).writeValue(Mockito.any(File.class), Mockito.any());

        List<Chore> chores = new ArrayList<>();
        boolean response = repository.save(chores);

        assertFalse(response);
    }

    @Test
    @DisplayName("#save > When able to write the chores on the file > Return true")
    void saveWhenAbleToWriteTheChoresOnTheFileReturnTrue() throws IOException {
        Mockito.doNothing().when(mapper).writeValue(Mockito.any(File.class), Mockito.any());

        List<Chore> chores = new ArrayList<>();
        boolean response = repository.save(chores);

        assertTrue(response);
    }

}
