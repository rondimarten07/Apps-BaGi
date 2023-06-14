# Menggunakan base image python versi 3.9
FROM python:3.9

ENV PORT 8080
ENV HOST 0.0.0.0

EXPOSE 8080

RUN apt-get update -y && \
    apt-get install -y python3-pip

# Mengatur working directory di dalam container
WORKDIR /app

# Menyalin file requirements.txt ke dalam container
COPY requirements.txt .

# Menginstal dependencies yang diperlukan
RUN pip install --no-cache-dir -r requirements.txt

# Menyalin folder uploads ke dalam container
COPY uploads ./uploads

# Menyalin file app.py ke dalam container
COPY app.py .

# Menjalankan aplikasi saat container berjalan
CMD ["python", "app.py"]
