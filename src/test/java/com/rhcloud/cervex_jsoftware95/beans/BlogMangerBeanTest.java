package com.rhcloud.cervex_jsoftware95.beans;

import com.rhcloud.cervex_jsoftware95.entities.Blog;
import com.rhcloud.cervex_jsoftware95.exceptions.InformationSystemException;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class BlogMangerBeanTest {

    private BlogMangerBean blogManagerBean;
    @PersistenceContext
    private EntityManager em; // Mocked EntityManager
    private UserManager um; // Mocked UserManager

    @Before
    public void setUp() {
        blogManagerBean = new BlogMangerBean();
        em = mock(EntityManager.class);
        um = mock(UserManager.class);
        // Inject mocks into bean
        blogManagerBean.em = em;
        blogManagerBean.um = um;
    }

    @Test
    public void testGetBlog_validId_returnsBlog() {
        String blogId = "testBlogId";
        Blog expectedBlog = new Blog();
        expectedBlog.setBlogID(blogId);
        when(em.find(Blog.class, blogId)).thenReturn(expectedBlog);

        Blog actualBlog = blogManagerBean.getBlog(blogId);

        assertThat(actualBlog).isEqualTo(expectedBlog);
    }

    @Test
    public void testGetBlog_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> blogManagerBean.getBlog(null));
    }

    @Test
    public void testGetBlog_noBlogFound_throwsInformationSystemException() {
        String blogId = "testBlogId";
        when(em.find(Blog.class, blogId)).thenReturn(null);

        assertThrows(InformationSystemException.class, () -> blogManagerBean.getBlog(blogId));
    }

    // Similar tests can be written for other methods like
    // getBlogWithComments, getDraft, getComment, etc.

    @Test
    public void testCreateBlog_validParams_returnsBlogId() {
        String blogTitle = "Test Blog Title";
        String blogIntro = "Test Blog Intro";
        String blogContent = "Test Blog Content";
        boolean isDraft = true;

        String expectedBlogId = "generatedId";
        Blog blog = new Blog();
        blog.setBlogID(expectedBlogId);
        em.persist(any(Blog.class));

        String actualBlogId = blogManagerBean.createBlog(blogTitle, blogIntro, blogContent, null, isDraft);

        assertThat(expectedBlogId).isEqualTo(actualBlogId);
        verify(um).getCurrentUser(); // Verify interaction with UserManager
    }

    @Test
    public void testCreateBlog_nullTitle_throwsNullPointerException() {
        String blogIntro = "Test Blog Intro";
        String blogContent = "Test Blog Content";
        boolean isDraft = true;

        assertThrows(NullPointerException.class, () -> blogManagerBean.createBlog(null, blogIntro, blogContent, null, isDraft));
    }

    @Test
    public void testCreateBlog_nullIntro_throwsNullPointerException() {
        String blogTitle = "Test Blog Title";
        String blogContent = "Test Blog Content";
        boolean isDraft = true;

        assertThrows(NullPointerException.class, () -> blogManagerBean.createBlog(blogTitle, null, blogContent, null, isDraft));
    }
}
