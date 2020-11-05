import axios from "axios";

const api = axios.create({
  baseURL: `${process.env.VUE_APP_API_BASE_URL}api`,
});
api.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(error)
);

export type IpInfo = {
  ipAddress: string;
  hostName: string;
};

export type HttpHeader = {
  name: string;
  value: string;
};

export type HttpHeaders = {
  headers: HttpHeader[];
};

export type HtmlEntity = {
  name: string;
  code: number;
  code2?: number;
  standard?: string;
  dtd?: string;
  description?: string;
  entityReference: string;
};

export type HtmlEntities = {
  entities: Array<HtmlEntity>;
};

export default class ApiService {
  async getIpAddress(): Promise<IpInfo> {
    const response = await api.get("ip");
    return response.data as IpInfo;
  }

  async getHttpHeader(): Promise<HttpHeaders> {
    const response = await api.get("http-headers");
    return response.data as HttpHeaders;
  }

  async getGeo(ipAddress: string): Promise<object> {
    const response = await api.get(`geo/${ipAddress}`);
    return response.data;
  }

  async getRdap(ipAddress: string): Promise<object> {
    const response = await api.get(`rdap/${ipAddress}`);
    return response.data;
  }

  async getHtmlEntities(): Promise<HtmlEntities> {
    const response = await api.get(`html-entities`);
    return response.data;
  }
}
