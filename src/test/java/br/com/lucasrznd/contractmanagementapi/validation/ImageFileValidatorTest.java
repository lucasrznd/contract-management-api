package br.com.lucasrznd.contractmanagementapi.validation;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class ImageFileValidatorTest {

    private final ImageFileValidator validator = new ImageFileValidator();

    @Test
    public void isValid_WithNullFile_ReturnsTrue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    public void isValid_WithEmptyFile_ReturnsTrue() {
        var file = new MockMultipartFile("image", "photo.png", "image/png", new byte[0]);

        assertThat(validator.isValid(file, null)).isTrue();
    }

    @Test
    public void isValid_WithImageFile_ReturnsTrue() {
        var file = new MockMultipartFile("image", "photo.png", "image/png", new byte[]{1, 2, 3});

        assertThat(validator.isValid(file, null)).isTrue();
    }

    @Test
    public void isValid_WithNonImageFile_ReturnsFalse() {
        var file = new MockMultipartFile("image", "contract.pdf", "application/pdf", new byte[]{1, 2, 3});

        assertThat(validator.isValid(file, null)).isFalse();
    }

    @Test
    public void isValid_WithoutContentType_ReturnsFalse() {
        var file = new MockMultipartFile("image", "photo", null, new byte[]{1, 2, 3});

        assertThat(validator.isValid(file, null)).isFalse();
    }

}
