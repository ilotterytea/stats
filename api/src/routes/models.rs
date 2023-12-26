use serde::Serialize;

#[derive(Serialize)]
pub struct Response<T> {
    pub status_code: i32,
    pub message: Option<String>,
    pub data: Option<T>,
}
