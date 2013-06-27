package com.nimble.http.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Date: 4/26/13
 * Time: 3:24 PM
 */
public abstract class AbstractModelAndViewFilter extends GenericFilterBean {

    protected Log logger = LogFactory.getLog(getClass());
    /**
     * LocaleResolver used by this servlet
     */
    private LocaleResolver localeResolver;
    /**
     * List of ViewResolvers used by this servlet
     */
    private List<ViewResolver> viewResolvers;
    /**
     * Detect all ViewResolvers or just expect "viewResolver" bean?
     */
    private boolean detectAllViewResolvers = true;

    /**
     * Set whether to detect all ViewResolver beans in this servlet's context. Otherwise,
     * just a single bean with name "viewResolver" will be expected.
     * <p>Default is "true". Turn this off if you want this servlet to use a single
     * ViewResolver, despite multiple ViewResolver beans being defined in the context.
     */
    public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
        this.detectAllViewResolvers = detectAllViewResolvers;
    }

    protected void render(Map model, String viewName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Determine locale for request and apply it to the response.
        Locale locale = this.localeResolver.resolveLocale(request);
        response.setLocale(locale);

        View view;
        //if (mv.isReference()) {
        // We need to resolve the view name.
        view = resolveViewName(viewName, model, locale, request);
        if (view == null) {
            throw new ServletException(
                    "Could not resolve view with name '" + viewName + "' in filter with name '" +
                            getFilterName() + "'");
        }
        //}
        /*else {
            // No need to lookup: the ModelAndView object contains the actual View object.
            view = mv.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
                        "View object in filter with name '" + getFilterName() + "'");
            }
        }*/

        // Delegate to the View object for rendering.
        if (logger.isDebugEnabled()) {
            logger.debug("Rendering view [" + view + "] in Filter with name '" + getFilterName() + "'");
        }
        view.render(model, request, response);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        initLocaleResolver(WebApplicationContextUtils.getWebApplicationContext(getServletContext()));
        initViewResolvers(WebApplicationContextUtils.getWebApplicationContext(getServletContext()));
    }

    /**
     * Initialize the ViewResolvers used by this class.
     * <p>If no ViewResolver beans are defined in the BeanFactory for this
     * namespace, we default to InternalResourceViewResolver.
     */
    private void initViewResolvers(ApplicationContext context) {
        this.viewResolvers = null;

        if (this.detectAllViewResolvers) {
            // Find all ViewResolvers in the ApplicationContext, including ancestor contexts.
            Map<String, ViewResolver> matchingBeans =
                    BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.values());
                // We keep ViewResolvers in sorted order.
                OrderComparator.sort(this.viewResolvers);
            }
        } else {
            try {
                ViewResolver vr = context.getBean(DispatcherServlet.VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
                this.viewResolvers = Collections.singletonList(vr);
            } catch (NoSuchBeanDefinitionException ex) {
                // Ignore, we'll add a default ViewResolver later.
            }
        }

        // Ensure we have at least one ViewResolver, by registering
        // a default ViewResolver if no other resolvers are found.
        if (this.viewResolvers == null) {
            this.viewResolvers = new LinkedList<ViewResolver>();
            this.viewResolvers.add(new org.springframework.web.servlet.view.InternalResourceViewResolver());
            if (logger.isDebugEnabled()) {
                logger.debug("No ViewResolvers found in filter '" + getFilterName() + "': using default");
            }
        }
    }

    /**
     * Resolve the given view name into a View object (to be rendered).
     * <p>The default implementations asks all ViewResolvers of this dispatcher.
     * Can be overridden for custom resolution strategies, potentially based on
     * specific model attributes or request parameters.
     *
     * @param viewName the name of the view to resolve
     * @param model    the model to be passed to the view
     * @param locale   the current locale
     * @param request  current HTTP servlet request
     * @return the View object, or <code>null</code> if none found
     * @throws Exception if the view cannot be resolved
     *                   (typically in case of problems creating an actual View object)
     * @see org.springframework.web.servlet.ViewResolver#resolveViewName
     */
    protected View resolveViewName(String viewName, Map<String, Object> model, Locale locale,
                                   HttpServletRequest request) throws Exception {

        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, locale);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    /**
     * Initialize the LocaleResolver used by this class.
     * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
     * we default to AcceptHeaderLocaleResolver.
     */
    private void initLocaleResolver(ApplicationContext context) {
        try {
            this.localeResolver = context.getBean(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
            if (logger.isDebugEnabled()) {
                logger.debug("Using LocaleResolver [" + this.localeResolver + "]");
            }
        } catch (NoSuchBeanDefinitionException ex) {
            // We need to use the default.
            this.localeResolver = new org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver();
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to locate LocaleResolver with name '" + DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME +
                        "': using default [" + this.localeResolver + "]");
            }
        }
    }

}
