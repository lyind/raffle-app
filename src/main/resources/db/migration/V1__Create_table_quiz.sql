CREATE TABLE quiz (
	id INTEGER PRIMARY KEY,
	ts DATETIME NOT NULL DEFAULT(strftime('%Y-%m-%d %H:%M:%f', 'now')),
	result TEXT NOT NULL
);