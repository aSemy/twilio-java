/**
 * This code was generated by
 * \ / _    _  _|   _  _
 *  | (_)\/(_)(_|\/| |(/_  v1.0.0
 *       /       /
 */

package com.twilio.rest.taskrouter.v1;

import com.twilio.base.Creator;
import com.twilio.converter.Promoter;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.exception.RestException;
import com.twilio.http.HttpMethod;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.Domains;

import java.net.URI;

public class WorkspaceCreator extends Creator<Workspace> {
    private final String friendlyName;
    private URI eventCallbackUrl;
    private String eventsFilter;
    private Boolean multiTaskEnabled;
    private String template;
    private Workspace.QueueOrder prioritizeQueueOrder;

    /**
     * Construct a new WorkspaceCreator.
     *
     * @param friendlyName A string to describe the Workspace resource
     */
    public WorkspaceCreator(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * The URL we should call when an event occurs. If provided, the Workspace will
     * publish events to this URL, for example, to collect data for reporting. See
     * <a href="https://www.twilio.com/docs/taskrouter/api/event">Workspace
     * Events</a> for more information..
     *
     * @param eventCallbackUrl The URL we should call when an event occurs
     * @return this
     */
    public WorkspaceCreator setEventCallbackUrl(final URI eventCallbackUrl) {
        this.eventCallbackUrl = eventCallbackUrl;
        return this;
    }

    /**
     * The URL we should call when an event occurs. If provided, the Workspace will
     * publish events to this URL, for example, to collect data for reporting. See
     * <a href="https://www.twilio.com/docs/taskrouter/api/event">Workspace
     * Events</a> for more information..
     *
     * @param eventCallbackUrl The URL we should call when an event occurs
     * @return this
     */
    public WorkspaceCreator setEventCallbackUrl(final String eventCallbackUrl) {
        return setEventCallbackUrl(Promoter.uriFromString(eventCallbackUrl));
    }

    /**
     * The list of Workspace events for which to call event_callback_url. For
     * example, if `EventsFilter=task.created, task.canceled,
     * worker.activity.update`, then TaskRouter will call event_callback_url only
     * when a task is created, canceled, or a Worker activity is updated..
     *
     * @param eventsFilter The list of Workspace events for which to call
     *                     event_callback_url
     * @return this
     */
    public WorkspaceCreator setEventsFilter(final String eventsFilter) {
        this.eventsFilter = eventsFilter;
        return this;
    }

    /**
     * Whether to enable multi-tasking. Can be: `true` to enable multi-tasking, or
     * `false` to disable it. However, all workspaces should be created as
     * multi-tasking. The default is `true`. Multi-tasking allows Workers to handle
     * multiple Tasks simultaneously. When enabled (`true`), each Worker can receive
     * parallel reservations up to the per-channel maximums defined in the Workers
     * section. In single-tasking mode (legacy mode), each Worker will only receive
     * a new reservation when the previous task is completed. Learn more at <a
     * href="https://www.twilio.com/docs/taskrouter/multitasking">Multitasking</a>..
     *
     * @param multiTaskEnabled Whether multi-tasking is enabled
     * @return this
     */
    public WorkspaceCreator setMultiTaskEnabled(final Boolean multiTaskEnabled) {
        this.multiTaskEnabled = multiTaskEnabled;
        return this;
    }

    /**
     * An available template name. Can be: `NONE` or `FIFO` and the default is
     * `NONE`. Pre-configures the Workspace with the Workflow and Activities
     * specified in the template. `NONE` will create a Workspace with only a set of
     * default activities. `FIFO` will configure TaskRouter with a set of default
     * activities and a single TaskQueue for first-in, first-out distribution, which
     * can be useful when you are getting started with TaskRouter..
     *
     * @param template An available template name
     * @return this
     */
    public WorkspaceCreator setTemplate(final String template) {
        this.template = template;
        return this;
    }

    /**
     * The type of TaskQueue to prioritize when Workers are receiving Tasks from
     * both types of TaskQueues. Can be: `LIFO` or `FIFO` and the default is `FIFO`.
     * For more information, see <a
     * href="https://www.twilio.com/docs/taskrouter/queue-ordering-last-first-out-lifo">Queue
     * Ordering</a>..
     *
     * @param prioritizeQueueOrder The type of TaskQueue to prioritize when Workers
     *                             are receiving Tasks from both types of TaskQueues
     * @return this
     */
    public WorkspaceCreator setPrioritizeQueueOrder(final Workspace.QueueOrder prioritizeQueueOrder) {
        this.prioritizeQueueOrder = prioritizeQueueOrder;
        return this;
    }

    /**
     * Make the request to the Twilio API to perform the create.
     *
     * @param client TwilioRestClient with which to make the request
     * @return Created Workspace
     */
    @Override
    @SuppressWarnings("checkstyle:linelength")
    public Workspace create(final TwilioRestClient client) {
        Request request = new Request(
            HttpMethod.POST,
            Domains.TASKROUTER.toString(),
            "/v1/Workspaces"
        );

        addPostParams(request);
        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Workspace creation failed: Unable to connect to server");
        } else if (!TwilioRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return Workspace.fromJson(response.getStream(), client.getObjectMapper());
    }

    /**
     * Add the requested post parameters to the Request.
     *
     * @param request Request to add post params to
     */
    private void addPostParams(final Request request) {
        if (friendlyName != null) {
            request.addPostParam("FriendlyName", friendlyName);
        }

        if (eventCallbackUrl != null) {
            request.addPostParam("EventCallbackUrl", eventCallbackUrl.toString());
        }

        if (eventsFilter != null) {
            request.addPostParam("EventsFilter", eventsFilter);
        }

        if (multiTaskEnabled != null) {
            request.addPostParam("MultiTaskEnabled", multiTaskEnabled.toString());
        }

        if (template != null) {
            request.addPostParam("Template", template);
        }

        if (prioritizeQueueOrder != null) {
            request.addPostParam("PrioritizeQueueOrder", prioritizeQueueOrder.toString());
        }
    }
}