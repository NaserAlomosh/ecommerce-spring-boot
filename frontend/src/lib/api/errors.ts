export type ApiErrorState = {
  title: string;
  message: string;
  status?: number;
};

export function toApiError(error: unknown): ApiErrorState {
  if (error instanceof Error) {
    const status = Number(error.message.match(/Request failed: (\d+)/)?.[1]);
    return {
      title: status ? `Request failed (${status})` : 'Something went wrong',
      message: status === 401
        ? 'Please login again to continue.'
        : status === 403
          ? 'You do not have permission to open this area.'
          : error.message,
      status: Number.isFinite(status) ? status : undefined,
    };
  }

  return { title: 'Unexpected error', message: 'Please try again.' };
}
