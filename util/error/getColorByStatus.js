function getColorByStatus(status) {
  return status >= 500
    ? 0xff0000 // Server Error
    : status >= 400
    ? 0xffa500 // Client Error
    : 0x0000ff; // Other Error
}

export default getColorByStatus;
